package it.unibo.oop.reactivegui02;

import it.unibo.oop.JFrameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = -6218820567019985015L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentGUI.class);
    private final JLabel display = new JLabel();

    /**
     * Builds and shows the GUI.
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton stop = new JButton("stop");
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        up.addActionListener(e -> agent.countUp());
        down.addActionListener(e -> agent.countDown());
        stop.addActionListener(e -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        new Thread(agent).start();
    }

    private final class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up = true;
        private int counter;

        @Override
        public void run() {
            while (!stop) {
                try {
                    counter += up ? 1 : -1;
                    /*
                     * Copy-pass + asynchronous update
                     *
                     * All the processing happens in the local thread, EDT is only given charge to
                     * update the UI. It may lose graphical updates, but frees the current thread
                     * immediately and does not overload EDT
                     */
                    final var todisplay = Integer.toString(counter);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            display.setText(todisplay);
                        }
                    });
                    /*
                     * With lambdas instead:
                     * SwingUtilities.invokeLater(() -> display.setText(todisplay));
                     */
                    Thread.sleep(100);
                } catch (final InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void countUp() {
            this.up = true;
        }

        public void countDown() {
            this.up = false;
        }
    }
}
