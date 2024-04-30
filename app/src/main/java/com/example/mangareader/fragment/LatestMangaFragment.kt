package com.example.mangareader.fragment

import com.example.mangareader.adapter.HomeChildFragmentRvAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import com.example.mangareader.model.Manga
import com.example.mangareader.service.ConnectivityObserver
import com.example.mangareader.service.NetworkConnectivityObserver
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LatestMangaFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var nestedScrollView: NestedScrollView
    lateinit var tvNoInternet: TextView
    lateinit var tvNoData: TextView
    var currentPage = 1

    companion object {
        fun newInstance() = LatestMangaFragment()
    }

    private val viewModel: LatestMangaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("LatestMangaFragment.onCreateView()")
        return inflater.inflate(R.layout.fragment_latest_manga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.loading_spinner)
        nestedScrollView = view.findViewById(R.id.scrollView)
        tvNoData = view.findViewById(R.id.tv_no_data)
        tvNoInternet = view.findViewById(R.id.tv_no_internet)
        recyclerView = view.findViewById(R.id.rv_latest_manga)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        val mangaList = mutableListOf<Manga>()
        val adapter = HomeChildFragmentRvAdapter(mangaList) {
            val intent = Intent(context, MangaDetailActivity::class.java)
            intent.putExtra("url",it.url)
            intent.putExtra("img",it.imageUrl)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangas ->
            if(mangaList.isEmpty()){
                mangaList.addAll(mangas)
                adapter.submitData(mangaList)
            }else{
                adapter.updateData(mangas)
            }
            progressBar.visibility = View.GONE
            if(mangas.isEmpty()){
                tvNoData.visibility = View.VISIBLE
                tvNoInternet.visibility = View.GONE
            }
        })

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight){
                progressBar.visibility = View.VISIBLE
                if(currentPage<=6 && isNetworkAvailable(requireContext())){
                    progressBar.visibility = View.VISIBLE
                    viewModel.fetchLatestMangas(currentPage++)
                }else if(currentPage>6){
                    Toast.makeText(context, "No more data", Toast.LENGTH_SHORT).show()
                }else if(!isNetworkAvailable(requireContext())){
                    tvNoInternet.visibility = View.VISIBLE
                    tvNoData.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        })

        tvNoInternet.setOnClickListener(View.OnClickListener {
            if(isNetworkAvailable(requireContext())){
                tvNoInternet.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                tvNoData.visibility = View.GONE
                viewModel.fetchLatestMangas(currentPage++)
            }else{
                tvNoInternet.visibility = View.VISIBLE
                tvNoData.visibility = View.VISIBLE
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })

        if(isNetworkAvailable(requireContext())){
            viewModel.fetchLatestMangas(currentPage++)
        }else{
            progressBar.visibility = View.GONE
            tvNoInternet.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
        }

    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onResume() {
        super.onResume()
        if(isNetworkAvailable(requireContext())){
            tvNoInternet.visibility = View.GONE
            tvNoData.visibility = View.GONE
        }else{
            tvNoInternet.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}
