package com.example.thierrycouilleault.blogphoto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.ViewHolder> {

    private Context context;

    private List<BlogPost> blogPosts;

    private FirebaseFirestore firebaseFirestore;

    public BlogPostAdapter(List<BlogPost> blogPosts) {

        this.blogPosts=blogPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context =parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        BlogPost blogPost = blogPosts.get(position);
        String descPost = blogPost.getDesc();

        holder.setDescBlog(descPost);

        String downloadUrl = blogPost.getImage_url();
        holder.setBlogImage(downloadUrl);

        long millisecond = blogPost.getTimestamp().getTime();

        String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(millisecond));

        holder.setTime(dateString);

        String user_id = blogPost.getUser_id();

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    holder.setData(userName,image);


                }else{
                    //handle error
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView desc,userName,blogPostDate;
        CircleImageView civPeople;
        ImageView ivPost;

        public ViewHolder(View itemView) {
            super(itemView);

            desc =itemView.findViewById(R.id.tv_desc_post);
            userName = itemView.findViewById(R.id.tv_user_name);
            blogPostDate =itemView.findViewById(R.id.tv_post_date);
            civPeople=itemView.findViewById(R.id.civ_people);
            ivPost=itemView.findViewById(R.id.iv_post_image);

        }

        public void setDescBlog(String descText){
            desc.setText(descText);
        }

        public void setBlogImage (String downloadUrl){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(downloadUrl)
                    .into(ivPost);
        }

        public void setTime(String time){

            blogPostDate.setText(time);
        }

        public void setData(String userName, String userImage){

            this.userName.setText(userName);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_avatar);
            Glide.with(context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(userImage)
                    .into(civPeople);
        }
    }
}


