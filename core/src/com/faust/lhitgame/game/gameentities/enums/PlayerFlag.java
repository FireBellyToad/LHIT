package com.faust.lhitgame.game.gameentities.enums;

/**
 * Player flags Enum.
 *
 * This flags represent all the boolean values that alter PlayerInstance status AND can be changed by environment or other GameInstances.
 *
 * For boolean values that can be changed by PlayerInstance only, use a private boolean in his class instead.
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum PlayerFlag {
    IS_SUBMERGED,
    IS_DEAD,
    PREPARE_END_GAME,
    IS_CHANGING_ROOM,
    GO_TO_GAMEOVER,
    PAUSE_GAME,
    HAS_KILLED_SECRET_BOSS,
    IS_CONFUSED
}
