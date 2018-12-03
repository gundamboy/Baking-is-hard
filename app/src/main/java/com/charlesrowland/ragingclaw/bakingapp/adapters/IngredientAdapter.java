package com.charlesrowland.ragingclaw.bakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.StepListActivity;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientHolder> {
    private final ArrayList<Ingredient> mIngredientList;
    private final StepListActivity mParentActivity;

    public IngredientAdapter(ArrayList<Ingredient> mIngredientList, StepListActivity mParentActivity) {
        this.mIngredientList = mIngredientList;
        this.mParentActivity = mParentActivity;
    }

    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item_alt, parent, false);
        return new IngredientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        String ingredientAmount = String.valueOf(mIngredientList.get(position).getQuantity());
        String ingredientMeasure = mIngredientList.get(position).getMeasure();
        String ingredientDescription = mIngredientList.get(position).getIngredient();

        holder.ingreientAmount.setText(ingredientAmount);
        holder.ingreientMeasurement.setText(ingredientMeasure);
        holder.ingredientDescription.setText(ingredientDescription);
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    class IngredientHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredientAmount) TextView ingreientAmount;
        @BindView(R.id.ingredientMeasurement) TextView ingreientMeasurement;
        @BindView(R.id.ingredientDescription) TextView ingredientDescription;

        public IngredientHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
