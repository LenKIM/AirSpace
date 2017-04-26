package com.yyy.xxx.airspace;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yyy.xxx.airspace.Model.Board;
import com.yyy.xxx.airspace.Model.BoardLab;

import java.util.List;

public class BoardFragment extends Fragment {

    private static final String ARG_ID = "uuid";

    public BoardFragment() {
        // Required empty public constructor
    }

    private RecyclerView mCardRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CardContentAdapter mAdapter;
    private List<Board> mBoards;

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

<<<<<<< HEAD
        mCardRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

=======
        mBoards = BoardLab.getBoardLab(getActivity()).getBoards();
        Log.d("GGGG", mBoards.size()+ "");

        mCardRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
>>>>>>>  - 쓸모없는 부분 정리 / 1차 완
        mAdapter = new CardContentAdapter(mBoards);
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

//        public ImageView mCard_imageView;
        private TextView mTitle;
        private TextView mDescription;
        private TextView mDate;
        private Board mBoard;

        public CardViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_board_fragment, parent, false));
            //TODO 이미지 클라우드 활용하기.
//            mCard_imageView = (ImageView) itemView.findViewById(R.id.card_Imageview);
            mTitle = (TextView) itemView.findViewById(R.id.card_name);
            mDescription = (TextView) itemView.findViewById(R.id.card_text);
            mDate = (TextView) itemView.findViewById(R.id.card_date);
        }

        public void bindBoard(Board board){
            mBoard = board;
            //여전히 사진은 Later
            mTitle.setText(mBoard.getTitle());
            mDescription.setText(mBoard.getDescription());
            mDate.setText(mBoard.getDate());
        }

        @Override
        public void onClick(View v) {
            //만약 내가 해당 리스트 아이템을 클릭했을 경우 발생하는 이벤트를 넣는 곳

        }
    }
    private class CardContentAdapter extends RecyclerView.Adapter<CardViewHolder> {

        private List<Board> mBoards;

        public CardContentAdapter(List<Board> boards) {
            this.mBoards = boards;
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {

            Board board = mBoards.get(position);

            //TODO 시간흐름에 따라 저장하는 방식으로 진행 할 것

//            holder.card_imageView.setImageDrawable(); //TODO 그림넣는 부분 해결해야함.
            holder.bindBoard(board);

        }

        @Override
        public int getItemCount() {
            return mBoards.size();
        }
        public void setBoards(List<Board> boards){
            mBoards = boards;
        }
    }
}
