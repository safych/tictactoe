import java.net.ServerSocket
import java.net.Socket
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.concurrent.thread
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.Serializable

data class Move(val player: Char, val row: Int, val col: Int) : Serializable
data class GameState(val board: Array<CharArray>, val currentPlayer: Char, val winner: Char?) : Serializable

class TicTacToeServer(private val port: Int) : JFrame("Tic Tac Toe Server") {

    private val board = Array(3) { CharArray(3) { ' ' } }
    private var currentPlayer = 'X'
    private var winner: Char? = null
    private val clients = mutableListOf<ObjectOutputStream>()
    private lateinit var serverSocket: ServerSocket
    private val clientSockets = mutableListOf<Socket>()
    private val buttons = Array(3) { arrayOfNulls<JButton>(3) }

    init {
        layout = GridLayout(3, 3)
        for (i in 0..2) {
            for (j in 0..2) {
                val button = JButton("")
                button.font = Font("Arial", Font.PLAIN, 60)
                button.addActionListener(ButtonListener(i, j))
                buttons[i][j] = button
                add(button)
            }
        }
        setSize(300, 300)
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }

    fun start() {
        serverSocket = ServerSocket(port)
        println("Server started on port $port")

        while (clients.size < 2) {
            val clientSocket = serverSocket.accept()
            val out = ObjectOutputStream(clientSocket.getOutputStream())
            val `in` = ObjectInputStream(clientSocket.getInputStream())
            clients.add(out)
            clientSockets.add(clientSocket)
            println("Client connected")

            thread {
                handleClient(clientSocket, `in`)
            }
        }
        broadcastGameState()
    }

    private fun handleClient(clientSocket: Socket, `in`: ObjectInputStream) {
        try {
            while (true) {
                val move = `in`.readObject() as Move
                if (makeMove(move)) {
                    broadcastGameState()
                    if (winner != null) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            `in`.close()
            clientSocket.close()
        }
    }

    @Synchronized
    private fun makeMove(move: Move): Boolean {
        if (board[move.row][move.col] == ' ' && move.player == currentPlayer) {
            board[move.row][move.col] = move.player
            buttons[move.row][move.col]?.text = move.player.toString()
            currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
            winner = checkWinner()
            return true
        }
        return false
    }

    private fun checkWinner(): Char? {
        for (i in 0..2) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0]
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i]
        }
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0]
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2]
        if (isFilledWithXAndO()) return 'A'
        return null
    }

    private fun isFilledWithXAndO(): Boolean {
        return board.all { row -> row.all { it == 'X' || it == 'O' } }
    }

    private fun broadcastGameState() {
        val array = Array(3) { CharArray(3) { ' ' } }
        for (i in 0..2) {
            for (j in 0..2) {
                array[i][j] = board[i][j]
            }
        }

        clients.forEach { it.writeObject(GameState(array, currentPlayer, winner)) }
    }

    fun stop() {
        clients.forEach { it.close() }
        clientSockets.forEach { it.close() }
        serverSocket.close()
    }

    inner class ButtonListener(private val row: Int, private val col: Int) : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            if (currentPlayer == 'X' && board[row][col] == ' ') {
                makeMove(Move('X', row, col))
                broadcastGameState()
            }
        }
    }
}
