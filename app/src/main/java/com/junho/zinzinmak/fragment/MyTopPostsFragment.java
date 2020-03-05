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
        // 그래 여기서 star 개수를 가지고 정렬을 하는데 지금 그 정렬이 이루어지지 않고 잇으니까
        // 문제가 되잖아. star 개수가 심지어 늘지도 않고 있잖아.
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }
}
