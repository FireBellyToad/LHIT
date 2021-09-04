package faust.lhitgame.menu.enums;

public enum MenuItem {
    YES,
    NO,
    BACK,
    CREDITS,
    EXIT_GAME,
    LOAD_GAME,
    ENGLISH,
    ITALIANO,
    NEW_GAME("menu.sure", new MenuItem[]{YES, NO}),
    PLAY_GAME(new MenuItem[]{NEW_GAME, LOAD_GAME, BACK}),
    GAME_OVER("menu.continue", new MenuItem[]{YES, NO}),
    MAIN(new MenuItem[]{PLAY_GAME, CREDITS, EXIT_GAME}),
    END_GAME(new MenuItem[]{BACK}),
    LANGUAGE(new MenuItem[]{ENGLISH, ITALIANO});

    final String titleMessageKey;
    final MenuItem[] subItems;

    MenuItem() {
        titleMessageKey = null;
        subItems = null;
    }

    MenuItem(MenuItem[] subItems) {
        titleMessageKey = null;
        this.subItems = subItems;
    }

    MenuItem(String title, MenuItem[] subItems) {
        this.titleMessageKey = title;
        this.subItems = subItems;
    }

    public MenuItem[] getSubItems() {
        return subItems;
    }

    public String getTitleMessageKey() {
        return titleMessageKey;
    }
}
