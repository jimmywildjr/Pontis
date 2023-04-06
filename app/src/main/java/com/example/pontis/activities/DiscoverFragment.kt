package com.example.pontis.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pontis.R
import com.example.pontis.adapters.MyOpportunityListAdapter
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.Opportunity


class DiscoverFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //use option menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }
    fun successOpportunityListFromFireStore(opportunityList: ArrayList<Opportunity>){
        val rv_opportunity = requireView().findViewById<RecyclerView>(R.id.rv_opportunity)
        rv_opportunity.layoutManager = LinearLayoutManager(activity)
        rv_opportunity.setHasFixedSize(true)
        val adapterOpportunity = MyOpportunityListAdapter(requireActivity(), opportunityList)
        rv_opportunity.adapter = adapterOpportunity
    }
    private fun getOpportunityListFromFireStore(){
        FirestoreClass().getOpportunityList(this)
    }

    override fun onResume() {
        super.onResume()
        getOpportunityListFromFireStore()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_opportunity_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.action_add_opportunity -> {
                startActivity(Intent(activity, AddOpportunityActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}