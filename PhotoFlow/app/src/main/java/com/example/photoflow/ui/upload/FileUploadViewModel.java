package com.example.photoflow.ui.upload;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.R;


public class FileUploadViewModel extends ViewModel {

    private final String base64EncodedFile;
    private boolean isUploading;
    private final FileRepository fileRepository;
    private final MutableLiveData<FileUploadFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<FileUploadResult> uploadResult = new MutableLiveData<>();

    FileUploadViewModel(String base64EncodedFile, FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.base64EncodedFile = base64EncodedFile;
        isUploading = false;
    }

    LiveData<FileUploadFormState> getFormState() {
        return formState;
    }

    LiveData<FileUploadResult> getUploadResult() {
        return uploadResult;
    }

    public void upload(String fileName, String description, String tags, String coordinates) {
        isUploading = true;
        fileRepository.upload(base64EncodedFile, fileName, description, coordinates, tags,
                new FileDataSource.FileCallback<Boolean>() {
                    @Override
                    public void onSuccess(Result<Boolean> result) {
                        isUploading = false;
                        Log.d("FileUploadViewModel", "Upload successful");
                        uploadResult.postValue(new FileUploadResult(true));
                    }

                    @Override
                    public void onError(Result.Error error) {
                        isUploading = false;
                        Log.e("FileUploadViewModel", "Upload failed: " + error.getError().getMessage());
                        uploadResult.postValue(new FileUploadResult("Upload failed: " + error.getError().getMessage()));
                    }
                });
    }

    public void uploadDataChanged(String fileName, String tags) {
        if (!isFileNameValid(fileName)) {
            formState.setValue(new FileUploadFormState(
                    R.string.invalid_file_name, // fileNameError
                    null,                       // tagsError
                    false                       // isDataValid
            ));
        } else if (!areTagsValid(tags)) {
            formState.setValue(new FileUploadFormState(
                    null,                       // fileNameError
                    R.string.invalid_tags,      // tagsError
                    false                       // isDataValid
            ));
        } else {
            formState.setValue(new FileUploadFormState(
                    null,                       // fileNameError
                    null,                       // tagsError
                    true                        // isDataValid
            ));
        }
    }


    private boolean isFileNameValid(String fileName) {
        return fileName != null && fileName.trim().length() > 2;
    }

    private boolean areTagsValid(String tags) {
        if (tags == null) return false;

        String[] splitTags = tags.split(",");
        for (String tag : splitTags) {
            String trimmed = tag.trim();
            if (trimmed.isEmpty() || !trimmed.matches("^[\\w\\- ]+$")) {
                return false;
            }
        }
        return true;
    }



}
