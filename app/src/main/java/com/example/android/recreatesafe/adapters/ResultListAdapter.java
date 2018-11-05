package com.example.android.recreatesafe.adapters;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.recreatesafe.utilities.Park;
import com.example.android.recreatesafe.R;
import java.util.List;
/**
 * Created by lizha on 10/27/2017.
 */

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ResultsViewHolder> {
    private static final String TAG = ResultListAdapter.class.getSimpleName();

    private Context mContext;
    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
//    ResultListLoaderAdapter mResultListLoaderAdapter;


    private ResultListFirebaseAdapter mResultListFirebaseAdapter;
    private List<Park> mParkList;
    private List<Double> mDisList;



    private int mNumberItems;

    public ResultListAdapter(Context context, LoaderManager loaderManager, List<Park> parkList, List<Double> disList, ListItemClickListener listener){
        mNumberItems = parkList.size();
        mOnClickListener = listener;
        viewHolderCount = 0;
        mContext = context;
        mParkList = parkList;
        mDisList = disList;

//        mResultListLoaderAdapter = new ResultListLoaderAdapter(mContext);
//        loaderManager.initLoader(Integer.parseInt(mContext.getResources().getString(R.string.loader_index_result_list)), args, mResultListLoaderAdapter );

    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.result_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ResultsViewHolder viewHolder = new ResultsViewHolder(view);

        viewHolder.mDistanceTextView.setText("ViewHolder index: " + viewHolderCount);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + viewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(mParkList.get(position), mDisList.get(position));
    }


    @Override
    public int getItemCount() {
        return mNumberItems;
    }





    public interface ListItemClickListener{
        void onListItemClick(int clickedIndex);
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView mLocationTextView;
        // Will display which ViewHolder is displaying this data
        TextView mDistanceTextView;

        TextView mNameTextView;
        TextView mTypeTextView;
        TextView mSafetyRatingTextView;
//        TextView mUserRatingTextView;
        public ResultsViewHolder(View itemView){
            super(itemView);
            mLocationTextView = (TextView) itemView.findViewById(R.id.tv_location);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.tv_distance);
            mNameTextView = (TextView) itemView.findViewById( R.id.tv_name);
            mTypeTextView = (TextView) itemView.findViewById((R.id.tv_type));
            mSafetyRatingTextView = (TextView) itemView.findViewById(R.id.tv_safety_rating);
//            mUserRatingTextView = (TextView) itemView.findViewById(R.id.tv_user_rating);

            itemView.setOnClickListener(this);


        }


        void bind(Park thisPark, double thisDis) {

            mNameTextView.setText(thisPark.getName());
            mTypeTextView.setText(thisPark.getType());

            mLocationTextView.setText(thisPark.getLocationShortText());
            mDistanceTextView.setText(String.format("%.2f", thisDis) + " km");
            mSafetyRatingTextView.setText("Danger Rating: " + thisPark.getSafetyRating());
//            mUserRatingTextView.setText("User Rating: " + thisPark.getUserRating());

        }



        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }


    }


}
