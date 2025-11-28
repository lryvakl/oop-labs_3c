package com.example.battleship;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // --- КОНСТАНТИ ---
    private final int SIZE = 10;
    private final int SHIP_CELLS_TOTAL = 20;
    private final int DELAY_COMPUTER_MS = 1000;

    // --- UI ЕЛЕМЕНТИ ---
    private GridLayout gridEnemy, gridPlayer;
    private TextView tvStatus;
    private Button btnRestart;

    // --- ДАНІ ГРИ ---
    // 0 - пусто, 1 - корабель, 2 - промах, 3 - влучив, 4 - знищений (хрестик на червоному)
    private int[][] enemyBoardData;
    private int[][] playerBoardData;

    private View[][] enemyViews = new View[SIZE][SIZE];
    private View[][] playerViews = new View[SIZE][SIZE];

    private int enemyShipsLeft;
    private int playerShipsLeft;

    private boolean isGameOver = false;
    private boolean isPlayerTurn = true;

    private final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        gridEnemy = findViewById(R.id.gridEnemy);
        gridPlayer = findViewById(R.id.gridPlayer);
        tvStatus = findViewById(R.id.tvStatus);
        btnRestart = findViewById(R.id.btnRestart);

        btnRestart.setOnClickListener(v -> startNewGame());

        startNewGame();
    }

    private void startNewGame() {
        enemyShipsLeft = SHIP_CELLS_TOTAL;
        playerShipsLeft = SHIP_CELLS_TOTAL;
        isGameOver = false;
        isPlayerTurn = true;

        tvStatus.setText("Ваш хід! Знищіть флот ворога.");

        enemyBoardData = new int[SIZE][SIZE];
        playerBoardData = new int[SIZE][SIZE];

        placeFleetRandomly(enemyBoardData);
        placeFleetRandomly(playerBoardData);

        drawGrid(gridEnemy, true);
        drawGrid(gridPlayer, false);
    }

    // ==========================================
    // ХІД ГРАВЦЯ
    // ==========================================
    private void onPlayerClick(int r, int c) {
        if (isGameOver || !isPlayerTurn) return;
        if (enemyBoardData[r][c] >= 2) return;

        View btn = enemyViews[r][c];
        animateCell(btn);

        if (enemyBoardData[r][c] == 1) {
            // Влучив (поки що просто червоний)
            enemyBoardData[r][c] = 3;
            btn.setBackgroundResource(R.drawable.cell_hit);
            enemyShipsLeft--;
            tvStatus.setText("Влучив! Стріляйте ще.");
            vibrate(100);

            // Перевіряємо на знищення
            checkAndMarkDestroyedShip(enemyBoardData, enemyViews, r, c, true);

            if (enemyShipsLeft == 0) {
                endGame(true);
            }
        } else {
            // Промах
            enemyBoardData[r][c] = 2;
            btn.setBackgroundResource(R.drawable.cell_miss);
            btn.setEnabled(false);
            tvStatus.setText("Промах. Хід ворога...");

            isPlayerTurn = false;
            new Handler(Looper.getMainLooper()).postDelayed(this::computerTurn, DELAY_COMPUTER_MS);
        }
    }

    // ==========================================
    // ХІД КОМП'ЮТЕРА
    // ==========================================
    private void computerTurn() {
        if (isGameOver) return;

        Point target = getComputerTarget();
        int r = target.x;
        int c = target.y;

        View cell = playerViews[r][c];
        animateCell(cell);

        if (playerBoardData[r][c] == 1) {
            // Комп'ютер влучив
            playerBoardData[r][c] = 3;
            cell.setBackgroundResource(R.drawable.cell_hit);
            playerShipsLeft--;
            tvStatus.setText("Ворог влучив у ваш корабель!");
            vibrate(100);

            checkAndMarkDestroyedShip(playerBoardData, playerViews, r, c, false);

            if (playerShipsLeft == 0) {
                endGame(false);
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(this::computerTurn, DELAY_COMPUTER_MS);
            }
        } else {
            // Комп'ютер промазав
            playerBoardData[r][c] = 2;
            cell.setBackgroundResource(R.drawable.cell_miss);
            tvStatus.setText("Ворог промазав. Ваш хід!");
            isPlayerTurn = true;
        }
    }

    // ==========================================
    // ПЕРЕВІРКА ЗНИЩЕННЯ І МАРКУВАННЯ
    // ==========================================
    private void checkAndMarkDestroyedShip(int[][] board, View[][] views, int r, int c, boolean isEnemyBoard) {
        // 1. Знаходимо межі корабля
        int top = r, bottom = r, left = c, right = c;

        // Розширюємо межі, поки бачимо частини корабля (статус 1 - живий, 3 - підбитий, 4 - знищений)
        while (top > 0 && isShipPart(board[top - 1][c])) top--;
        while (bottom < SIZE - 1 && isShipPart(board[bottom + 1][c])) bottom++;
        while (left > 0 && isShipPart(board[r][left - 1])) left--;
        while (right < SIZE - 1 && isShipPart(board[r][right + 1])) right++;

        // 2. Перевіряємо, чи є живі частини (статус 1)
        boolean isDead = true;
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (board[i][j] == 1) {
                    isDead = false;
                    break;
                }
            }
        }

        // 3. Якщо знищений - маркуємо і корабель, і воду навколо
        if (isDead) {
            if (isEnemyBoard) {
                tvStatus.setText("Корабель знищено!");
                vibrate(300);
            }

            // А) Маркуємо сам корабель ХРЕСТИКАМИ (новий ресурс)
            for (int i = top; i <= bottom; i++) {
                for (int j = left; j <= right; j++) {
                    if (board[i][j] == 3) { // Якщо це підбита частина
                        board[i][j] = 4; // Новий статус "Знищений остаточно"
                        views[i][j].setBackgroundResource(R.drawable.cell_destroyed);
                    }
                }
            }

            // Б) Маркуємо воду навколо (Halo)
            for (int i = top - 1; i <= bottom + 1; i++) {
                for (int j = left - 1; j <= right + 1; j++) {
                    if (isValidCoordinate(i, j)) {
                        // Якщо це не частина корабля і не промах
                        if (!isShipPart(board[i][j]) && board[i][j] != 2) {
                            board[i][j] = 2;
                            views[i][j].setBackgroundResource(R.drawable.cell_miss);
                            views[i][j].setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    // Допоміжний метод: чи є це частина корабля (жива, підбита або знищена)
    private boolean isShipPart(int value) {
        return value == 1 || value == 3 || value == 4;
    }

    // "Мозок" комп'ютера
    private Point getComputerTarget() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (playerBoardData[i][j] == 3) { // Шукаємо підбитий, але ще не знищений (статус 3)
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] dir : directions) {
                        int newR = i + dir[0];
                        int newC = j + dir[1];
                        if (isValidCoordinate(newR, newC) && playerBoardData[newR][newC] <= 1) {
                            return new Point(newR, newC);
                        }
                    }
                }
            }
        }
        Random random = new Random();
        int r, c;
        do {
            r = random.nextInt(SIZE);
            c = random.nextInt(SIZE);
        } while (playerBoardData[r][c] >= 2);
        return new Point(r, c);
    }

    // --- ДОПОМІЖНІ МЕТОДИ ---

    private void endGame(boolean playerWon) {
        isGameOver = true;
        tvStatus.setText(playerWon ? "ПЕРЕМОГА!" : "ПОРАЗКА!");
        vibrate(500);
        showGameOverDialog(playerWon);
    }

    private void animateCell(View v) {
        v.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    private void vibrate(int milliseconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(milliseconds);
        }
    }

    private void showGameOverDialog(boolean playerWon) {
        new AlertDialog.Builder(this)
                .setTitle(playerWon ? "ПЕРЕМОГА!" : "ПОРАЗКА")
                .setMessage(playerWon ? "Ви знищили ворожий флот!" : "Ваш флот потоплено.")
                .setIcon(playerWon ? android.R.drawable.star_big_on : android.R.drawable.ic_delete)
                .setCancelable(false)
                .setPositiveButton("Нова гра", (dialog, which) -> startNewGame())
                .setNegativeButton("Вихід", (dialog, which) -> finish())
                .show();
    }

    private boolean isValidCoordinate(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    private void drawGrid(GridLayout grid, boolean isEnemy) {
        grid.removeAllViews();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = (int) (screenWidth / 12);

        for (int i = 0; i <= SIZE; i++) {
            for (int j = 0; j <= SIZE; j++) {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(1, 1, 1, 1);

                if (i == 0 && j == 0) {
                    TextView empty = new TextView(this);
                    empty.setLayoutParams(params);
                    grid.addView(empty);
                } else if (i == 0) {
                    TextView letter = new TextView(this);
                    letter.setText(LETTERS[j - 1]);
                    letter.setGravity(Gravity.CENTER);
                    letter.setTypeface(null, Typeface.BOLD);
                    letter.setTextColor(Color.BLACK);
                    letter.setLayoutParams(params);
                    grid.addView(letter);
                } else if (j == 0) {
                    TextView number = new TextView(this);
                    number.setText(String.valueOf(i));
                    number.setGravity(Gravity.CENTER);
                    number.setTypeface(null, Typeface.BOLD);
                    number.setTextColor(Color.BLACK);
                    number.setLayoutParams(params);
                    grid.addView(number);
                } else {
                    int row = i - 1;
                    int col = j - 1;
                    View cell;

                    if (isEnemy) {
                        Button btn = new Button(this);
                        btn.setPadding(0, 0, 0, 0);
                        btn.setBackgroundColor(Color.parseColor("#E3F2FD"));
                        enemyViews[row][col] = btn;
                        btn.setOnClickListener(v -> onPlayerClick(row, col));
                        cell = btn;
                    } else {
                        View v = new View(this);
                        if (playerBoardData[row][col] == 1) {
                            v.setBackgroundResource(R.drawable.cell_ship);
                        } else {
                            v.setBackgroundColor(Color.parseColor("#E3F2FD"));
                        }
                        playerViews[row][col] = v;
                        cell = v;
                    }
                    cell.setLayoutParams(params);
                    grid.addView(cell);
                }
            }
        }
    }

    private void placeFleetRandomly(int[][] board) {
        int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        Random random = new Random();

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int r = random.nextInt(SIZE);
                int c = random.nextInt(SIZE);
                boolean vertical = random.nextBoolean();

                if (isValidPlacement(board, r, c, size, vertical)) {
                    placeShip(board, r, c, size, vertical);
                    placed = true;
                }
            }
        }
    }

    private boolean isValidPlacement(int[][] board, int r, int c, int size, boolean vertical) {
        if (vertical) {
            if (r + size > SIZE) return false;
        } else {
            if (c + size > SIZE) return false;
        }
        int startRow = Math.max(0, r - 1);
        int endRow = vertical ? Math.min(SIZE - 1, r + size) : Math.min(SIZE - 1, r + 1);
        int startCol = Math.max(0, c - 1);
        int endCol = vertical ? Math.min(SIZE - 1, c + 1) : Math.min(SIZE - 1, c + size);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (board[i][j] == 1) return false;
            }
        }
        return true;
    }

    private void placeShip(int[][] board, int r, int c, int size, boolean vertical) {
        for (int i = 0; i < size; i++) {
            if (vertical) {
                board[r + i][c] = 1;
            } else {
                board[r][c + i] = 1;
            }
        }
    }
}