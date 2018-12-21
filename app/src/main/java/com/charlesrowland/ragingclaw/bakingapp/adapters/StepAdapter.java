package com.charlesrowland.ragingclaw.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.StepDetailActivity;
import com.charlesrowland.ragingclaw.bakingapp.StepDetailFragment;
import com.charlesrowland.ragingclaw.bakingapp.StepActivity;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepHolder> {
    private ArrayList<Recipe> mRecipeArrayList;
    private final ArrayList<Step> mStepList;
    private final StepActivity mParentActivity;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();

            if(mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(AllMyConstants.ARG_ITEM_ID, item.getId());
                arguments.putParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE, mRecipeArrayList);
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, StepDetailActivity.class);
                intent.putExtra(AllMyConstants.ARG_ITEM_ID, item.getId());
                intent.putParcelableArrayListExtra(AllMyConstants.RECIPE_ARRAYLIST_STATE, mRecipeArrayList);

                context.startActivity(intent);
            }
        }
    };

    public StepAdapter(StepActivity parent, ArrayList<Step> mStepList, ArrayList<Recipe> mRecipeArrayList, Boolean mTwoPane) {
        this.mParentActivity = parent;
        this.mRecipeArrayList = mRecipeArrayList;
        this.mStepList = mStepList;
        this.mTwoPane = mTwoPane;
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item, parent, false);
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
