package com.junho.zinzinmak.fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopPostsFragment extends PostListFragment {

    FirebaseAuth mAuth;

    public MyTopPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        mAuth = FirebaseAuth.getInstance();
        // [START my_top_posts_query]
        // My top posts by number of stars
        String myUserId = mAuth.getCurrentUser().getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }
}
