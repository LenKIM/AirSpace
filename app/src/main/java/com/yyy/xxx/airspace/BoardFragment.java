package com.yyy.xxx.airspace;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyy.xxx.airspace.Model.Board;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;

public class BoardFragment extends Fragment {


    public BoardFragment() {
        // Required empty public constructor
    }

    private RecyclerView mCardRecyclerView;
    private CardContentAdapter mAdapter;
    private ArrayList<Board> mBoards;

    public static BoardFragment newInstance(MapPoint param1, String param2) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1.toString());
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        mBoards = new ArrayList<>();

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mCardRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        mAdapter = new CardContentAdapter(getActivity(), mBoards);
        mCardRecyclerView.setAdapter(mAdapter);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private static class CardViewHolder extends RecyclerView.ViewHolder {

        public ImageView card_imageView;
        public TextView name;
        public TextView desciption;

        public CardViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_board_fragment, parent, false));

            card_imageView = (ImageView) itemView.findViewById(R.id.card_Imageview);
            name = (TextView) itemView.findViewById(R.id.card_name);
            desciption = (TextView) itemView.findViewById(R.id.card_text);

        }
    }
    private class CardContentAdapter extends RecyclerView.Adapter<CardViewHolder> {

        private Context mContext;
        private ArrayList<Board> mBoards;

        public CardContentAdapter(Context context, ArrayList<Board> boards) {
            mContext = context;
            mBoards = boards;
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {

            Board board = mBoards.get(position);
//            holder.card_imageView.setImageDrawable(); //TODO 그림넣는 부분 해결해야함.
            holder.name.setText(board.getName());
            holder.desciption.setText(board.getDescription());
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
