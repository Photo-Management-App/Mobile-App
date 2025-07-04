package com.example.photoflow.ui.home;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private AlbumAdapter albumAdapter;
//    private AlbumRepository albumRepository;
//    private ProgressBar progressBar;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        recyclerView = root.findViewById(R.id.albumRecyclerView);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        progressBar = root.findViewById(R.id.loading);
//        // Initialize the AlbumRepository
//        AlbumDataSource albumDataSource = new AlbumDataSource(requireContext());
//        albumRepository = AlbumRepository.getInstance(albumDataSource, requireContext());
//        progressBar.setVisibility(View.VISIBLE);
//        albumRepository.getAlbumItems(new AlbumDataSource.AlbumCallback<List<AlbumItem>>() {
//            @Override
//            public void onSuccess(Result<List<AlbumItem>> result) {
//                if (result instanceof Result.Success) {
//                    List<AlbumItem> albumItems = ((Result.Success<List<AlbumItem>>) result).getData();
//                    albumAdapter = new AlbumAdapter(albumItems, item -> {
//                        // Handle album click
//                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("albumItem", item);
//                        navController.navigate(R.id.nav_album_detail_fragment, bundle);
//                    });
//                    recyclerView.setAdapter(albumAdapter);
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(getContext(), "Failed to load albums", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//
//        return root;
//    }
//
}