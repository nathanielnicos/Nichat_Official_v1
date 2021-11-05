package id.nns.nichat.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.FragmentHomeBinding
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.ui.my_profile.ProfileActivity
import id.nns.nichat.utils.ConnectionChecker
import id.nns.nichat.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private lateinit var preference: UserPreference
    private lateinit var adapter: HomeAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var connectionChecker: ConnectionChecker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        preference = UserPreference(requireContext())

        adapter = HomeAdapter(preference.getUser().uid)
        binding.rvHome.adapter = adapter

        connectionChecker = ConnectionChecker(requireContext())
        observe()

        binding.toolbarHome.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun observe() {
        connectionChecker.observe(viewLifecycleOwner) {
            homeViewModel.updateChannels(it)

            if (it) {
                binding.connectionIndicator
                    .setImageResource(R.drawable.ic_online_indicator)
            } else {
                binding.connectionIndicator
                    .setImageResource(R.drawable.ic_offline_indicator)
            }
        }

        homeViewModel.channels.observe(viewLifecycleOwner) {
            adapter.channels = it as ArrayList<Channel>
        }

        homeViewModel.channelsCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.animationEmpty.visibility = View.GONE
            } else {
                binding.animationEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.tvTitleHome.text = resources.getString(R.string.title, preference.getUser().name)
        Glide.with(this)
            .load(preference.getUser().imgUrl)
            .placeholder(R.drawable.profile)
            .into(binding.civHome)
    }

}
