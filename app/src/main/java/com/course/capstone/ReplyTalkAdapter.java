package com.course.capstone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.course.capstone.models.Comment;
import com.course.capstone.models.CommentInterface;
import com.course.capstone.models.DataManager;
import com.course.capstone.models.Qna;
import com.course.capstone.models.QnaInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ReplyTalkAdapter extends RecyclerView.Adapter<ReplyTalkAdapter.ViewHolder> {
    Context context;
    List<Comment> items;
    String qnaname;
    String personid;
    String title;
    String qcontent;
    String qdate;
    int commentcount;
    int likecount;
    String qid;
    ArrayList<String>likepeoplelist;
    ArrayList<String>commentpeoplelist;

    DataManager dataManager=DataManager.getInstance();
    ImageButton btn_r_menu;

    public ReplyTalkAdapter(Context context, List<Comment> items,String qnaname,String personid, String title, String content, String date, int commentcount, int likecount,String qid, ArrayList<String> likepeoplelist,ArrayList<String> commentpeople) {
        this.context = context;
        this.items = items;
        this.personid=personid;
        this.qnaname = qnaname;
        this.title = title;
        this.qcontent = content;
        this.qdate = date;
        this.commentcount = commentcount;
        this.likecount = likecount;
        this.qid = qid;
        this.likepeoplelist = likepeoplelist;
        this.commentpeoplelist=commentpeople;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply_list, parent, false);

        return new ViewHolder(view);
    }

    //온바인드뷰홀더는 아이템을 세팅하거나 스크롤링 할때 호출되는 애다. 때문에 position이 필요하다.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.no.setText(items.get(position).getR_no());
//        holder.t_no.setText(items.get(position).getR_t_no());
        holder.name.setText(items.get(position).getCommentpeople());
        holder.content.setText(items.get(position).getCommentcontent());
        holder.date.setText(items.get(position).getCommentdate());
        if(dataManager.getUser().getUserid().equals(items.get(position).getCommentpeople()))
        {
            btn_r_menu.setVisibility(ImageButton.VISIBLE);

        }
        else{
            btn_r_menu.setVisibility(ImageButton.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Comment item) {
        items.add(item);
        notifyDataSetChanged();
    }

    //뷰홀더라는 애는 아이템안에 들어갈 텍스트등의 내용을 초기화 하는 역할이다.
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, name, date;



        public ViewHolder(View itemView) {
            super(itemView);
            btn_r_menu= itemView.findViewById(R.id.btn_r_menu);
            btn_r_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment= new Comment(items.get(getAdapterPosition()).getCommentpeople(), items.get(getAdapterPosition()).getCommentcontent(), items.get(getAdapterPosition()).getCommentcontent(), items.get(getAdapterPosition()).getParentid());
                    final PopupMenu popupMenu = new PopupMenu(context, itemView);
                    popupMenu.getMenuInflater().inflate(R.menu.coment_popup, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.action_delete) {

                                comentdelete( items.get(getAdapterPosition()).getCommentid());
                                commentpeoplelist.remove(dataManager.getUser().getUserid());

                                Qna qna = new Qna(qnaname, personid, title, qcontent, qdate, commentcount, likecount, qid, likepeoplelist,commentpeoplelist);
                                update(qna);


                            } else if (menuItem.getItemId() == R.id.action_recommentwrite) {
                               // Intent intent=new Intent(context,)
                              //  comment_rewrite(comment,items.get(getAdapterPosition()).getCommentid());

                            } else {

                            }

                            return false;
                        }

                    });
                    popupMenu.show();
                }
            });

            content = itemView.findViewById(R.id.s_content);
            name = itemView.findViewById(R.id.s_name);
            date = itemView.findViewById(R.id.tv_text_date);

        }

    }
    //게시글 삭제하는 함수
    public void comentdelete(String commnetid) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-139-15-252.us-east-2.compute.amazonaws.com:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CommentInterface commentInterface = retrofit.create(CommentInterface.class);
        Call<Void> call = commentInterface.removeComment(commnetid);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse1: Something");


                    Toast.makeText(context, "댓글이 삭제되었습니다", Toast.LENGTH_LONG).show();

                } else {
                    Log.d(TAG, "onResponse1: Something Wrong");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "목록을 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                ;
                Log.d(TAG, "onFailure2: 게시물 목록 왜안나와");
            }
        });
    }
        public  void update(Qna qna) {


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-139-15-252.us-east-2.compute.amazonaws.com:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            QnaInterface retrofitService = retrofit.create(QnaInterface.class);
            Call<Qna> call = retrofitService.updateQna(qid,qna);

            call.enqueue(new Callback<Qna>() {
                @Override
                public void onResponse(Call<Qna> call, Response<Qna> response) {



                }

                @Override
                public void onFailure(Call<Qna> call, Throwable t) {
                    Log.e("좋아요 기능 실패ㅠㅠ", t.getMessage());
                }

            });
        }

    }


