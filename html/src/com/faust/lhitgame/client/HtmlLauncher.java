package com.faust.lhitgame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.saves.impl.HtmlSaveFileManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;

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

        @Override
        public Preloader.PreloaderCallback getPreloaderCallback() {
                return createPreloaderPanel(GWT.getHostPageBaseURL() + "loading_splash.png");
        }

        @Override
        protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
                meterPanel.setStyleName("gdx-meter");
                meterPanel.addStyleName("nostripes");
                meterStyle.setProperty("backgroundColor", "#000000");
                meterStyle.setProperty("backgroundImage", "none");
        }
}