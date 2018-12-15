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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.ViewHolder> {

    private Context context;

    private List<BlogPost> blogPosts;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogPostAdapter(List<BlogPost> blogPosts) {

        this.blogPosts=blogPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context =parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        BlogPost blogPost = blogPosts.get(position);

        final String blogPostId = blogPost.BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String descPost = blogPost.getDesc();

        holder.setDescBlog(descPost);

        String downloadUrl = blogPost.getImage_url();
        String thumbUri = blogPost.getThumb_image();
        holder.setBlogImage(downloadUrl,thumbUri);

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

        //Get Likes count

        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();
                    holder.updateLikesCount(count);

                }else{

                    holder.updateLikesCount(0);
                }
            }
        });

            // Get Likes
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.ivLikes.setImageDrawable(context.getDrawable(R.drawable.action_like));

                }else{

                    holder.ivLikes.setImageDrawable(context.getDrawable(R.drawable.action_like_gray));
                }

            }
        });



        //Likes features

        holder.ivLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes")

                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String,Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likeMap);

                        }else{
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
                        }

                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView desc,userName,blogPostDate, likes_counter;
        private CircleImageView civPeople;
        private  ImageView ivPost,ivLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            desc =itemView.findViewById(R.id.tv_desc_post);
            userName = itemView.findViewById(R.id.tv_user_name);
            blogPostDate =itemView.findViewById(R.id.tv_post_date);
            civPeople=itemView.findViewById(R.id.civ_people);
            ivPost=itemView.findViewById(R.id.iv_post_image);
            ivLikes=itemView.findViewById(R.id.iv_like_btn);
            likes_counter=itemView.findViewById(R.id.tv_counter_likes);

        }

        public void setDescBlog(String descText){
            desc.setText(descText);
        }

        public void setBlogImage (String downloadUrl, String thumbUri ){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(downloadUrl)
                    .thumbnail(Glide.with(context).load(thumbUri))
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

        public void updateLikesCount(int count){

            likes_counter.setText(count+ " Likes");
        }
    }
}


