package com.lacavedeharol.chess.app.game;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * Manages sound playback for the game.
 */
public class SoundManager {

    private static SoundManager instance;
    private final Map<String, Clip> clipCache = new HashMap<>();
    private boolean isMuted = false;

    private SoundManager() {
    }

    /**
     * Returns the singleton instance of SoundManager.
     * 
     * @return the singleton instance of SoundManager
     */
    public static synchronized SoundManager getInstance() {
        return instance != null ? instance : (instance = new SoundManager());
    }

    /**
     * Sets the muted state of the sound manager.
     * 
     * @param muted true to mute the sound manager, false to unmute it
     */
    public void setMuted(boolean muted) {
        this.isMuted = muted;
    }

    /**
     * Returns the muted state of the sound manager.
     * 
     * @return true if the sound manager is muted, false otherwise
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Plays a sound.
     * 
     * @param name the name of the sound to play
     */
    public void playSound(String name) {
        if (isMuted)
            return;

        Clip clip = clipCache.computeIfAbsent(name, this::loadSoundInternal);

        if (clip != null) {
            if (clip.isRunning())
                clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Internal method to load a sound clip.
     * 
     * @param name the name of the sound to load
     * @return the loaded Clip, or null if loading failed
     */
    private Clip loadSoundInternal(String name) {
        try {
            String path = "/sounds/" + name + ".wav";
            InputStream is = SoundManager.class.getResourceAsStream(path);

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

            AudioFormat format = audioIn.getFormat();

            if (format.getSampleSizeInBits() > 16 || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat newFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false);

                audioIn = AudioSystem.getAudioInputStream(newFormat, audioIn);
                format = newFormat;
            }

            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioIn);

            return clip;
        } catch (Exception e) {
            return null;
        }
    }

}
