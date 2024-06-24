import java.net.Socket
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class TicTacToeClient(val ip: String, val port: Int) : JFrame() {

    private val board = Array(3) { arrayOfNulls<JButton>(3) }
    private lateinit var `in`: ObjectInputStream
    private lateinit var out: ObjectOutputStream
    private lateinit var socket: Socket
    private var currentPlayer = 'X'

    init {
        title = "Tic Tac Toe"
        setSize(300, 300)
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = GridLayout(3, 3)
        isVisible = true

        for (i in 0..2) {
            for (j in 0..2) {
                val button = JButton("")
                button.font = Font("Arial", Font.PLAIN, 60)
                button.addActionListener(ButtonListener(i, j))
                board[i][j] = button
                add(button)
            }
        }

        connectToServer()
    }

    private fun connectToServer() {
        socket = Socket(ip, port)
        out = ObjectOutputStream(socket.getOutputStream())
        `in` = ObjectInputStream(socket.getInputStream())

        Thread {
            try {
                while (true) {
                    val state = `in`.readObject() as GameState
                    updateBoard(state)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                `in`.close()
                out.close()
                socket.close()
            }
        }.start()
    }

    private fun updateBoard(state: GameState) {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j]?.text = state.board[i][j].toString()
            }
        }
        currentPlayer = state.currentPlayer
        if (state.winner != null && state.winner != 'A') {
            JOptionPane.showMessageDialog(this, "Winner is ${state.winner}")
        }
        if(state.winner != null && state.winner == 'A') {
            JOptionPane.showMessageDialog(this, "Draw!")
        }
    }

    inner class ButtonListener(private val row: Int, private val col: Int) : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            out.writeObject(Move(currentPlayer, row, col))
        }
    }
}
