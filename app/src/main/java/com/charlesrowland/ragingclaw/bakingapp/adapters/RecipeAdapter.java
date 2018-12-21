package com.charlesrowland.ragingclaw.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.IngredientsActivity;
import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.StepActivity;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private final Context mContext;
    private final ArrayList<Recipe> mRecipeList;
    private List<Ingredient> mIngredientList;
    private String mJsonResult;
    private String mRecipeJson;
    private Boolean mTwoPane;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, int recipeId);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecipeAdapter(Context mContext, ArrayList<Recipe> mRecipeList, String mJsonResult, Boolean mTwoPane) {
        this.mContext = mContext;
        this.mRecipeList = mRecipeList;
        this.mJsonResult = mJsonResult;
        this.mTwoPane = mTwoPane;
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Resources res = holder.itemView.getContext().getResources();

        holder.name.setText(mRecipeList.get(position).getName());

        String servingsText = res.getString(R.string.servings_text) + " " + String.valueOf(mRecipeList.get(position).getServings());
        holder.servings.setText(servingsText);

        mIngredientList = mRecipeList.get(position).getIngredients();
        String ingredientsText = res.getString(R.string.ingredients_text) + " " + String.valueOf(mIngredientList.size());
        holder.ingredientsCount.setText(ingredientsText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = mRecipeList.get(holder.getAdapterPosition());
                ArrayList<Recipe> currentRecipeArrayList = new ArrayList<>();
                currentRecipeArrayList.add(recipe);
                mRecipeJson = jsonToString(mJsonResult, holder.getAdapterPosition());

                Intent intent;
                if (mTwoPane) {
                    intent = new Intent(mContext, StepActivity.class);
                } else {
                    intent = new Intent(mContext, IngredientsActivity.class);
                }
                intent.putParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA, currentRecipeArrayList);
                intent.putExtra(AllMyConstants.RECIPE_INTENT_JSON_EXTRA, mRecipeJson);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    class RecipeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.servings) TextView servings;
        @BindView(R.id.totalIngredients) TextView ingredientsCount;


        public RecipeHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // this turns the selected recipe json into a string.
    // momma always said, "work smarter, not harder"
    // ok she didn't actually say that but whatever, it's not wrong.
    private String jsonToString(String jsonResult, int position) {
        JsonElement jsonElement = new JsonParser().parse(jsonResult);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonElement recipeJsonElement = jsonArray.get(position);
        return recipeJsonElement.toString();
    }
}
