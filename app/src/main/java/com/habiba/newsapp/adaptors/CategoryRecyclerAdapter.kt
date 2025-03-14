import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.habiba.newsapp.R

class CategoryRecyclerAdapter(
    private val categoryList: List<String>,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder>() {

    private var selectedPosition: Int = 0 // 🔹 Default selection to first item

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val categoryCard: CardView = itemView.findViewById(R.id.categoryCard)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_recyclerview_item, parent, false)
        return CategoryViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category = categoryList[position]
        holder.categoryText.text = category

        // 🔹 Set "General" (First Item) as Selected by Default
        if (position == selectedPosition) {
            holder.categoryCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCF24162"))
        } else {
            holder.categoryCard.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#102540"))
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousSelectedPosition) // Unselect previous
            notifyItemChanged(selectedPosition) // Highlight new selection

            onCategorySelected(category)
        }
    }
}
