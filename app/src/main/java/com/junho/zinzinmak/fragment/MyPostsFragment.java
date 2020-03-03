package com.junho.zinzinmak.fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    private FirebaseAuth mAuth;
    public MyPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        mAuth = FirebaseAuth.getInstance();

        // All my posts
        return databaseReference.child("user-posts").child(mAuth.getCurrentUser().getUid());
    }
}
