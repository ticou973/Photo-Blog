package com.example.thierrycouilleault.blogphoto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView;
    private List<BlogPost> blogPosts;
    private BlogPostAdapter blogPostAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        blogListView =view.findViewById(R.id.blog_list_view);
        firebaseAuth = FirebaseAuth.getInstance();
        blogPosts=new ArrayList<>();

        if(firebaseAuth.getCurrentUser()!=null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                            blogPosts.add(blogPost);
                            blogPostAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
        blogPostAdapter = new BlogPostAdapter(blogPosts);
        blogListView.setLayoutManager(new LinearLayoutManager(getContext()));
        blogListView.setHasFixedSize(true);
        blogListView.setAdapter(blogPostAdapter);

        return view;
    }

}
