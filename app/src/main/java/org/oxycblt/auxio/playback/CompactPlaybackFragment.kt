package org.oxycblt.auxio.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.databinding.FragmentCompactPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore

/**
 * A [Fragment] that displays the currently played song at a glance, with some basic controls.
 * Extends into [PlaybackFragment] when clicked on.
 *
 * Instantiation is done by FragmentContainerView, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class CompactPlaybackFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCompactPlaybackBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        // Put a placeholder song in the binding & hide the playback fragment initially.
        binding.song = MusicStore.getInstance().songs[0]
        binding.playbackModel = playbackModel
        binding.executePendingBindings()

        binding.root.apply {
            setOnClickListener {
                findNavController().navigate(
                    MainFragmentDirections.actionGoToPlayback()
                )
            }

            setOnLongClickListener {
                detailModel.navToItem(playbackModel.song.value!!)
                true
            }
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                logD("Updating song display to ${song.name}")

                binding.song = song
                binding.playbackProgress.max = song.seconds.toInt()
            }
        }

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            binding.playbackPlayPause.setPlaying(it, playbackModel.canAnimate)

            playbackModel.enableAnimation()
        }

        logD("Fragment Created")

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        playbackModel.disableAnimation()
    }
}
