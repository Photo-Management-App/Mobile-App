//package com.example.photoflow.ui.home;
//
//public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
//
//    private static final String TAG = "AlbumAdapter";
//    private FileRepository fileRepository;
//
//
//    public interface OnAlbumClickListener {
//        void onAlbumClick(AlbumItem albumItem);
//    }
//
//    private List<AlbumItem> albumList;
//    private OnAlbumClickListener listener;
//
//    public AlbumAdapter(List<AlbumItem> albumList, OnAlbumClickListener listener) {
//        this.albumList = albumList;
//        this.listener = listener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        AlbumItem albumItem = albumList.get(position);
//
//
//
//    }
//
//
//
//}
