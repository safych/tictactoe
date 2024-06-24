import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

fun main() {
    val frame = JFrame("Tic Tac Toe - Choose Mode")
    val panel = JPanel()
    val button1 = JButton("Host")
    val button2 = JButton("Connect")

    button1.addActionListener {
        frame.dispose()
        HostSetupFrame()
    }

    button2.addActionListener {
        frame.dispose()
        ConnectFrame()
    }

    panel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    panel.layout = GridLayout(2, 1)
    panel.add(button1)
    panel.add(button2)

    frame.add(panel, BorderLayout.CENTER)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}
