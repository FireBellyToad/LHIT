package faust.lhipgame.menu.enums;

public enum MenuItem {
    YES,
    NO,
    BACK,
    MUSIC_TOGGLE,
    SOUND_TOGGLE,
    CREDITS,
    EXIT_GAME,
    LOAD_GAME,
    NEW_GAME("Are you sure?",new MenuItem[]{YES,NO}),
    PLAY_GAME(new MenuItem[]{NEW_GAME,LOAD_GAME,BACK}),
    GAME_OVER("Continue?", new MenuItem[]{YES,NO}),
    OPTIONS("Options",new MenuItem[]{BACK, MUSIC_TOGGLE,SOUND_TOGGLE}),
    MAIN(new MenuItem[]{PLAY_GAME,OPTIONS,CREDITS,EXIT_GAME});

    String title;
    MenuItem[] subItems;

    MenuItem() {
        title = null;
        subItems = null;
    }

    MenuItem(MenuItem[] subItems) {
        title = null;
        this.subItems = subItems;
    }

    MenuItem(String title, MenuItem[] subItems) {
        this.title = title;
        this.subItems = subItems;
    }

    public MenuItem[] getSubItems() {
        return subItems;
    }

    public String getTitle() {
        return title;
    }
}
