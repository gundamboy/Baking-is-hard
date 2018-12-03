package com.charlesrowland.ragingclaw.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.StepDetailActivity;
import com.charlesrowland.ragingclaw.bakingapp.StepDetailFragment;
import com.charlesrowland.ragingclaw.bakingapp.StepListActivity;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepHolder> {
    private final ArrayList<Step> mStepList;
    private final StepListActivity mParentActivity;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();
            if(mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(StepDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, StepDetailActivity.class);
                intent.putExtra(StepDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));

                context.startActivity(intent);
            }

        }
    };

    public StepAdapter(StepListActivity parent, ArrayList<Step> mStepList, Boolean mTwoPane) {
        this.mParentActivity = parent;
        this.mStepList = mStepList;
        this.mTwoPane = mTwoPane;
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item_alt, parent, false);
        return new StepHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepHolder holder, int position) {
        String num = String.valueOf(mStepList.get(position).getId()) + ": ";
        holder.stepNum.setText(num);

        holder.stepShortDescription.setText(mStepList.get(position).getShortDescription());

        holder.itemView.setTag(mStepList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }

    class StepHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stepNum) TextView stepNum;
        @BindView(R.id.stepShortDescription) TextView stepShortDescription;

        public StepHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
