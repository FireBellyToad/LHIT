package com.faust.lhitgame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.saves.HtmlSaveFileManager;

public class HtmlLauncher extends GwtApplication {

        final static int SCALE_FACTOR = 4;

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
//                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(LHITGame.GAME_WIDTH * SCALE_FACTOR, LHITGame.GAME_HEIGHT * SCALE_FACTOR);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LHITGame(new HtmlSaveFileManager());
        }
}