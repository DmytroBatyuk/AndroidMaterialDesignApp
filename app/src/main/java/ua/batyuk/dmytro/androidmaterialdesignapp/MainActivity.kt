package ua.batyuk.dmytro.androidmaterialdesignapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = Adapter()

        /*
         * Initialization of ItemTouchHelper (which exists out of the box)
         */
        var itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter.getContract()))
        recyclerView.adapter = adapter

        /*
         * Attaching ItemTouchHelper to RecyclerView to receive moving callbacks
         */
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}


/**
 * Callback which notifies RecyclerView adapter about moving items in the list
 */
interface MoveContract {
    fun onMove(fromPos: Int, toPos: Int)
}


/**
 * Implementation of ItemTouchHelper.Callback which notifies RecyclerView adapter about changes
 *  through MoveContract callback
 */
private class ItemTouchHelperCallback(val moveContract: MoveContract) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /*
         * Setting up emit only on dragging flags
         */
        return makeMovementFlags(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        /*
         * Notify adapter through callback about changes
         */
        moveContract.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Leave it empty because we don't support swiping in concrete implementation
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
            if (viewHolder is ViewHolder) {
                viewHolder.isDragged(true)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ViewHolder) {
            viewHolder.isDragged(false)
        }
    }
}

private class Adapter : RecyclerView.Adapter<ViewHolder>() {
    private val items: ArrayList<Int> = arrayListOf()

    init {
        /*
         * Populate adapters list
         */
        for (i in 1..15) {
            items.add(i)
        }
    }

    /**
     * Callback's implementation
     */
    private val moveContract = object : MoveContract {
        override fun onMove(fromPos: Int, toPos: Int) {
            /*
             * Remove item from the list from old position and insert to new position
             * Notify adapter about item's movement
             */
            val i = items[fromPos]
            items.removeAt(fromPos)
            items.add(toPos, i)
            notifyItemMoved(fromPos, toPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, null))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getContract(): MoveContract {
        return moveContract
    }
}

private class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private var title: TextView = view.findViewById(R.id.title)
    private var description: TextView = view.findViewById(R.id.description)

    init {
    }

    fun bind(position: Int) {
        title.text = "Title #$position"
        description.text = "Description #$position text is over here"
    }

    fun isDragged(isDragged: Boolean) {
        Log.e("DIMA", "isDragged=$isDragged")
        view.elevation = if (isDragged) 100f else 0f
    }
}