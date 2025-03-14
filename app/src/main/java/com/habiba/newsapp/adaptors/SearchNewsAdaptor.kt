import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.habiba.newsapp.R
import com.habiba.newsapp.responce.Article

class SearchNewsAdaptor(
    private var articles: List<Article>,
) : RecyclerView.Adapter<SearchNewsAdaptor.SearchNewsAdaptorViewHolder>() {

    class SearchNewsAdaptorViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val imageNews: ImageView = itemView.findViewById(R.id.newsImage)
        val source: TextView = itemView.findViewById(R.id.newsSource)
        val headline: TextView = itemView.findViewById(R.id.newsHeadline)
        val content: TextView = itemView.findViewById(R.id.newscontent)
        val date:TextView=itemView.findViewById(R.id.Newsdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNewsAdaptorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.searchnewsitem, parent, false)
        return SearchNewsAdaptorViewHolder(view)
    }

    override fun onBindViewHolder(holder:SearchNewsAdaptorViewHolder , position: Int) {
        val newsItem = articles[position]

        Log.d("NewsRecyclerView", "🔹 Binding ViewHolder - Position: $position, Title: ${newsItem.title}")

        Glide.with(holder.itemView.context)
            .load(newsItem.urlToImage)
            .placeholder(R.drawable.newsphoto)
            .error(R.drawable.no_image_found)
            .into(holder.imageNews)

        holder.headline.text = newsItem.title ?: "No Title"
        holder.source.text = newsItem.source.name ?: "Unknown Source"
        holder.content.text = newsItem.content ?: "No Content"
        holder.date.text = newsItem.publishedAt.substringBefore("T")
    }

    override fun getItemCount(): Int = articles.size

    fun updateNews(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()  // 🔥 Refresh RecyclerView when news updates
    }

}
