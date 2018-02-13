package com.cwm.incube;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class news extends AppCompatActivity {

    private RecyclerView newsblog ;
    private DatabaseReference mDatabase ;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_news);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        newsblog = (RecyclerView) findViewById(R.id.news_list) ;
        newsblog.setHasFixedSize(true);
        newsblog.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setText(model.getText());
                viewHolder.setImage(getApplicationContext(),model.getImage()) ;
            }
        } ;

        newsblog.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView ;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView ;
        }

        public void setText(String title) {
            TextView post_title =(TextView) mView.findViewById(R.id.news_text);
            post_title.setText(title);
        }

        public void setImage(Context ctx, String image){
            ImageView newsimage = (ImageView) mView.findViewById(R.id.news_image);
            Picasso.with(ctx).load(image).into(newsimage);
        }
    }
}
