package com.identity_crisis.thesamaritans.activity.newsfeed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.identity_crisis.thesamaritans.adapter.NewsfeedAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.Data

class NewsfeedActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var progressBar: ProgressBar
    val database = FirebaseDatabase.getInstance()

    val ref = database.getReference("/")
    var myDataset = mutableListOf<Data>()


    val myRef1 = database.getReference("One")
    val myRef2 = database.getReference("Two")
    val myRef3 = database.getReference("Three")
    val myRef4 = database.getReference("Four")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfeed)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE
        Log.d("NewsfeedActivity", "Fetching Data")
        fetchData()
        Log.d("NewsfeedActivity", "Data fetched")
        progressBar.visibility = View.GONE

        viewManager = LinearLayoutManager(this)
//        viewAdapter = NewsfeedAdapter(myDataset, this)



        recyclerView = findViewById<RecyclerView>(R.id.rcview).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


//
//
//
//        myRef1.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                var arr = mutableListOf<String>()
//                for (data in p0.children) {
//                    arr.add(data.value.toString())
//                }
//
//                myDataset.add(
//                    Data(
//                        info1 = arr[0],
//                        info2 = arr[1],
//                        info3 = arr[2],
//                        name = arr[3],
//                        img = arr[4]
//                    )
//                )
//                viewAdapter.notifyDataSetChanged()
//                progressBar.visibility = View.INVISIBLE
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//        })
//        myRef2.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                var arr = mutableListOf<String>()
//                for (data in p0.children) {
//                    arr.add(data.value.toString())
//                }
//
//                myDataset.add(
//                    Data(
//                        info1 = arr[0],
//                        info2 = arr[1],
//                        info3 = arr[2],
//                        name = arr[3],
//                        img = arr[4]
//                    )
//                )
//                viewAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//
//        myRef3.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                var arr = mutableListOf<String>()
//                for (data in p0.children) {
//                    arr.add(data.value.toString())
//                }
//
//                myDataset.add(
//                    Data(
//                        info1 = arr[0],
//                        info2 = arr[1],
//                        info3 = arr[2],
//                        name = arr[3],
//                        img = arr[4]
//                    )
//                )
//                viewAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//
//        myRef4.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                var arr = mutableListOf<String>()
//                for (data in p0.children) {
//                    arr.add(data.value.toString())
//                }
//
//                myDataset.add(
//                    Data(
//                        info1 = arr[0],
//                        info2 = arr[1],
//                        info3 = arr[2],
//                        name = arr[3],
//                        img = arr[4]
//                    )
//                )
//                viewAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })


        val itemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    myDataset.removeAt(position)
                    recyclerView.adapter!!.notifyItemRemoved(position)
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    val intent = Intent(applicationContext, FeedInfo::class.java)
                    startActivity(intent)
                    recyclerView.adapter!!.notifyItemChanged(position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun fetchData() {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val fetchedData = it.getValue(Data::class.java)
                    if (fetchedData != null) {
                        myDataset.add(fetchedData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("NewsfeedActivity", "Some error has cancelled the fetching process")
            }

        })
    }

}