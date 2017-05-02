package com.yyy.xxxx.airspace2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yyy.xxxx.airspace2.Model.Board;
import com.yyy.xxxx.airspace2.Model.BoardLab;

import java.util.List;

public class BoardFragment extends Fragment {

    private static final String TAG = BoardFragment.class.getName();
    private static final String ARG_ID = "uuid";

    public BoardFragment() {
        // Required empty public constructor
    }

    private RecyclerView mCardRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CardContentAdapter mAdapter;
    private List<Board> mBoards;
    public RequestManager mGlideRequestManager;

    public static BoardFragment newInstance(String id) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mBoards = BoardLab.getBoardLab(getActivity()).getBoards();

        mGlideRequestManager = Glide.with(getActivity());

        mCardRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mAdapter = new CardContentAdapter(mBoards, mGlideRequestManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mCardRecyclerView.setLayoutManager(linearLayoutManager);
        mCardRecyclerView.setAdapter(mAdapter);

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mCard_imageView;
        private TextView mTitle;
        private TextView mDescription;
        private TextView mDate;
        private Board mBoard;

        private RequestManager mGlideRequestManager;


        public CardViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_board_fragment, parent, false));
            //TODO 이미지 클라우드 활용하기.
            mCard_imageView = (ImageView) itemView.findViewById(R.id.card_Imageview);
            mTitle = (TextView) itemView.findViewById(R.id.card_name);
            mDescription = (TextView) itemView.findViewById(R.id.card_text);
            mDate = (TextView) itemView.findViewById(R.id.card_date);
        }

        public void bindBoard(Board board, RequestManager glideRequestManager){

            mBoard = board;

            mGlideRequestManager = glideRequestManager;

            mTitle.setText(mBoard.getTitle());
            mDescription.setText(mBoard.getDescription());
            mDate.setText(mBoard.getDate());


            if (mBoard.getPhotoUri() == null){
                mGlideRequestManager.load(R.drawable.a)
                        .into(mCard_imageView);
            }else {
                Uri temp = Uri.parse(mBoard.getPhotoUri());

                mGlideRequestManager.load(temp)
                        .centerCrop()
                        .error(R.drawable.a)
                        .into(mCard_imageView);
                Log.d("URI", mBoard.getPhotoUri());
            }
        }

        @Override
        public void onClick(View v) {
            //만약 내가 해당 리스트 아이템을 클릭했을 경우 발생하는 이벤트를 넣는 곳

        }
    }
    private class CardContentAdapter extends RecyclerView.Adapter<CardViewHolder> {

        private List<Board> mBoards;
        private RequestManager mGlideRequestManager;


        public CardContentAdapter(List<Board> boards, RequestManager glideRequestManager) {
            this.mBoards = boards;
            this.mGlideRequestManager = glideRequestManager;
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {

            Board board = mBoards.get(position);

            //TODO 시간흐름에 따라 저장하는 방식으로 진행 할 것
            holder.bindBoard(board, mGlideRequestManager);
        }

        @Override
        public int getItemCount() {
            return mBoards.size();
        }
    }
}
