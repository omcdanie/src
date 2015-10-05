package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class SoundUtility implements Runnable {

    private boolean running = true;
    private volatile static SoundUtility instance = null;
    private final ArrayList<String> fileToPlay = new ArrayList<>();

    private boolean playSounds = true;

    public static SoundUtility getInstance() {
        if (instance == null) {
            synchronized (SoundUtility.class) {
                if (instance == null) // Double-Check!
                {
                    instance = new SoundUtility();
                }
            }
        }
        return instance;
    }

    private SoundUtility() {
        new Thread(this).start();
    }

    public void setPlaySounds(boolean value) {
        playSounds = value;
    }

    private boolean getPlaySounds() {
        return playSounds;
    }

    private void playSound() {
        if (!getPlaySounds()) {
            return;
        }

        String next = getNextFileToPlay();
        while (next != null) {
            try {
                Clip clip = null;                    // The sound clip

                AudioInputStream source = AudioSystem.getAudioInputStream(new File(next));
                DataLine.Info clipInfo = new DataLine.Info(Clip.class, source.getFormat());
                if (AudioSystem.isLineSupported(clipInfo)) {
                    // Create a local clip to avoid discarding the old clip
                    Clip newClip = (Clip) AudioSystem.getLine(clipInfo);   // Create the clip
                    newClip.open(source);

                    // Deal with previous clip
                    if (clip != null) {
                        if (clip.isActive()) // If it's active
                        {
                            clip.stop();                      // ...stop it
                        }
                        if (clip.isOpen()) // If it's open...
                        {
                            clip.close();                     // ...close it.
                        }
                    }
                    clip = newClip;                       // We have a clip, so discard old
                } else {
                    System.err.println("Unsupported sound - cannot play " + next);
                    System.err.println("Turning off sounds");
                    setPlaySounds(false);
                    return;
                }

                clip.loop(0);
            } catch (UnsupportedAudioFileException e) {
                JOptionPane.showMessageDialog(null, "File not supported",
                        "Unsupported File Type", JOptionPane.WARNING_MESSAGE);
            } catch (LineUnavailableException e) {
                JOptionPane.showMessageDialog(null, "Clip not available", "Clip Error",
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "I/O error creating clip: " + e.getMessage(), "Clip Error",
                        JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException npe) {
                System.err.println("Unable to play sound clip.");
            }
            next = getNextFileToPlay();
        }
    }

    public synchronized void playSound(String file) {
        addFileToPlay(file);
        notify();
    }

    @Override
    public void run() {
        System.out.println("SoundUtility Running...");

        while (isRunning()) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            playSound();
        }
        System.out.println("SoundUtility Shut Down...");
    }

    private boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
        notifyAll();
    }

    private String getNextFileToPlay() {
        if (fileToPlay.isEmpty()) {
            return null;
        }
        String next = fileToPlay.get(0);
        fileToPlay.remove(0);
        return next;
    }

    private void addFileToPlay(String newFileToPlay) {
        fileToPlay.add(newFileToPlay);
    }

}
