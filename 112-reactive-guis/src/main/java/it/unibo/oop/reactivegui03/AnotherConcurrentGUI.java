package it.unibo.oop.reactivegui03;

import it.unibo.oop.JFrameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);

    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    private final CounterAgent counterAgent = new CounterAgent();

    /**
     * Builds a C3GUI.
     */
    public AnotherConcurrentGUI() {
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                counterAgent.upCounting();
            }
        });
        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                counterAgent.downCounting();
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AnotherConcurrentGUI.this.stopCounting();
            }
        });
        new Thread(counterAgent).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAITING_TIME);
                } catch (final InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
                AnotherConcurrentGUI.this.stopCounting();
            }
        }).start();
    }

    private void stopCounting() {
        counterAgent.stopCounting();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });
    }

    private final class CounterAgent implements Runnable, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        private volatile boolean stop;
        private volatile boolean up = true;
        private int counter;

        @Override
        public void run() {
            while (!stop) {
                try {
                    final var nextText = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            display.setText(nextText);
                        }
                    });
                    counter += up ? 1 : -1;
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        void stopCounting() {
            this.stop = true;
        }

        void upCounting() {
            this.up = true;
        }

        void downCounting() {
            this.up = false;
        }
    }
}
