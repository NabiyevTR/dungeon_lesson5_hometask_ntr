package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class UnitController {
    private GameController gc;
    private MonsterController monsterController;
    private Hero hero;
    private Unit currentUnit;
    private int index;
    private List<Unit> allUnits;


    public MonsterController getMonsterController() {
        return monsterController;
    }

    public Hero getHero() {
        return hero;
    }

    public boolean isItMyTurn(Unit unit) {
        return currentUnit == unit;
    }

    public boolean isCellFree(int cellX, int cellY) {
        for (Unit u : allUnits) {
            if (u.getCellX() == cellX && u.getCellY() == cellY) {
                return false;
            }
        }
        return true;
    }

    public UnitController(GameController gc) {
        this.gc = gc;
        this.hero = new Hero(gc);
        this.monsterController = new MonsterController(gc);
    }

    public void init() {
        this.monsterController.activate(5, 5);
        this.monsterController.activate(9, 5);
        this.index = -1;
        this.allUnits = new ArrayList<>();
        this.allUnits.add(hero);
        this.allUnits.addAll(monsterController.getActiveList());
        this.nextTurn();
    }

    public void nextTurn() {

        index++;
        if (index >= allUnits.size()) {
            index = 0;
        }
        currentUnit = allUnits.get(index);

        // 5. Попробуйте посчитать раунды ( каждый раз, когда ход переходит к игроку
        // номер раунда должен увеличиваться )
        if (currentUnit.unitType == Unit.UnitType.HERO) {
            gc.incGameRound();
        }

        // 6. В начале 3 раунда должен появиться новый монстр ( * каждого третьего )
        if (gc.getGameRound() % 3 == 0 && currentUnit.unitType == Unit.UnitType.HERO) {
            allUnits.add(gc.getUnitController().getMonsterController().getNewMonster());
        }

        // 7. В начале хода персонажи восстанавливают 1 хп
        if (currentUnit.hp < currentUnit.hpMax) {
            currentUnit.hp++;
        }

        currentUnit.startTurn();
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        hero.render(batch, font18);
        monsterController.render(batch, font18);
    }

    public void update(float dt) {
        hero.update(dt);
        monsterController.update(dt);

        if (!currentUnit.isActive() || currentUnit.getTurns() == 0) {
            nextTurn();
        }
    }

    public void removeUnitAfterDeath(Unit unit) {
        int unitIndex = allUnits.indexOf(unit);
        allUnits.remove(unit);
        if (unitIndex <= index) {
            index--;
        }
    }
}
