import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField

class ConnectFrame : JFrame("Tic Tac Toe - Connect to Server") {

    init {
        val panel = JPanel()
        val ipField = JTextField(15)
        val portField = JTextField(5)
        val connectButton = JButton("Connect")

        connectButton.addActionListener {
            val ip = ipField.text
            val port = portField.text.toInt()
            TicTacToeClient(ip, port)
            dispose()
        }

        panel.add(JTextField("IP:"))
        panel.add(ipField)
        panel.add(JTextField("Port:"))
        panel.add(portField)
        panel.add(connectButton)

        add(panel, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}
