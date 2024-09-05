package com.kessoku.bocchifrog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.kessoku.bocchifrog.player.Difficulty;
import com.kessoku.bocchifrog.player.PlayerCharacter;
import com.kessoku.bocchifrog.player.PlayerConfig;
import com.kessoku.bocchifrog.rendering.Renderer;

public class MainActivity extends AppCompatActivity {


    private final OnClickListener welcomeScreenClickListener = v -> setupCharacterSelectScreen();

    private static MainActivity instance;

    private TextView nameValidationTextField;
    private TextView characterTextField;
    private TextView difficultyTextField;
    private TextView selectionValidationTextField;

    private final PlayerConfig playerConfig = new PlayerConfig();

    private final TextWatcher nameInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            String validationStr = generateNameValidationText(editable.toString());
            nameValidationTextField.setText(validationStr);
            playerConfig.setName(editable.toString());
        }
    };

    private void setupWelcomeScreen() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.welcome_screen)
                .setOnClickListener(welcomeScreenClickListener);
    }

    public static String generateNameValidationText(String nameStr) {
        if (nameStr == null) {
            return "Name must not be null";
        }

        if (nameStr.length() > 32) {
            return "Name must be 32 or fewer characters";
        }

        if (nameStr.isEmpty()) {
            return "Name must not be empty";
        }

        if (nameStr.trim().length() == 0) {
            return "Name cannot just be whitespace";
        }

        return "";
    }

    public String generateSelectionValidationText() {
        if (!generateNameValidationText(playerConfig.getName()).isEmpty()) {
            return "Invalid name";
        }

        if (playerConfig.getCharacter() == null
                || playerConfig.getCharacter() == PlayerCharacter.NULL) {
            return "Please select a character";
        }

        if (playerConfig.getDifficulty() == null
                || playerConfig.getDifficulty() == Difficulty.NULL) {
            return "Please select a difficulty";
        }

        return "";
    }

    public static void resetCharacterSelectScreen() {
        instance.setupCharacterSelectScreen();
    }

    private void setupCharacterSelectScreen() {
        setContentView(R.layout.character_select);

        EditText nameInput = findViewById(R.id.name_input);
        nameValidationTextField = findViewById(R.id.name_validation_text);
        nameInput.addTextChangedListener(nameInputWatcher);

        characterTextField = findViewById(R.id.character_name);
        difficultyTextField = findViewById(R.id.difficulty_text);
        selectionValidationTextField = findViewById(R.id.selection_validation_text);
    }

    public void selectBocchi(View view) {
        playerConfig.setCharacter(PlayerCharacter.BOCCHI);
        characterTextField.setText("Play as: Frocchi");
    }
    public void selectKita(View view) {
        playerConfig.setCharacter(PlayerCharacter.KITA);
        characterTextField.setText("Play as: Kitaaa~");
    }
    public void selectNijika(View view) {
        playerConfig.setCharacter(PlayerCharacter.NIJIKA);
        characterTextField.setText("Play as: Nijika the Dorito Girl");
    }
    public void selectRyo(View view) {
        playerConfig.setCharacter(PlayerCharacter.RYO);
        characterTextField.setText("Play as: Ryo (aka Roy)");
    }

    public void selectEasy(View view) {
        playerConfig.setDifficulty(Difficulty.EASY);
        difficultyTextField.setText("Difficulty: Easy");
    }

    public void selectNormal(View view) {
        playerConfig.setDifficulty(Difficulty.NORMAL);
        difficultyTextField.setText("Difficulty: Normal ");
    }

    public void selectHard(View view) {
        playerConfig.setDifficulty(Difficulty.HARD);
        difficultyTextField.setText("Difficulty: Hard");
    }

    public void startGame(View view) {
        String validationText = generateSelectionValidationText();
        if (validationText.isEmpty()) {
            setupGameScreen();
        } else {
            selectionValidationTextField.setText(validationText);
        }
    }

    private void setupGameScreen() {
        setContentView(R.layout.game_layout);

        GameView gameView = findViewById(R.id.game_view);
        gameView.initializeGame(playerConfig);

        Renderer renderer = new Renderer(gameView, 60);
        renderer.beginRendering();
    }

    public static void exit() {
        instance.finish();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_main);
        setupWelcomeScreen();
    }
}