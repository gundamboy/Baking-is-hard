package com.charlesrowland.ragingclaw.bakingapp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.IngredientsActivity;
import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.StepListActivity;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientHolder> {
    private final ArrayList<Ingredient> mIngredientList;
    private final IngredientsActivity mParentActivity;

    public IngredientAdapter(IngredientsActivity mParentActivity, ArrayList<Ingredient> mIngredientList) {
        this.mIngredientList = mIngredientList;
        this.mParentActivity = mParentActivity;
    }

    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        String ingredientAmount = String.valueOf(mIngredientList.get(position).getQuantity());
        String ingredientMeasure = mIngredientList.get(position).getMeasure();
        String ingredientDescription = mIngredientList.get(position).getIngredient();

        holder.ingredientAmount.setText(ingredientAmount);
        holder.ingredientMeasurement.setText(ingredientMeasure);
        holder.ingredientDescription.setText(ingredientDescription);
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    class IngredientHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredientAmount) TextView ingredientAmount;
        @BindView(R.id.ingredientMeasurement) TextView ingredientMeasurement;
        @BindView(R.id.ingredientDescription) TextView ingredientDescription;

        public IngredientHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
