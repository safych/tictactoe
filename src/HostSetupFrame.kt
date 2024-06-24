import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import kotlin.concurrent.thread

class HostSetupFrame : JFrame("Tic Tac Toe - Host Setup") {

    init {
        val panel = JPanel()
        val portField = JTextField(5)
        val startButton = JButton("Start Server")

        startButton.addActionListener {
            val port = portField.text.toInt()
            val server = TicTacToeServer(port)
            thread {
                server.start()
            }
            addWindowListener(object : java.awt.event.WindowAdapter() {
                override fun windowClosing(windowEvent: java.awt.event.WindowEvent) {
                    server.stop()
                    System.exit(0)
                }
            })
            dispose()
        }

        panel.add(JTextField("Port:"))
        panel.add(portField)
        panel.add(startButton)

        add(panel, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}
