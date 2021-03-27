package faust.lhipgame.menu;

public enum MenuItem {
    MUSIC_TOGGLE,
    SOUND_TOGGLE,
    NEW_GAME,
    LOAD_GAME,
    OPTIONS(new MenuItem[]{MUSIC_TOGGLE,SOUND_TOGGLE}),
    MAIN(new MenuItem[]{NEW_GAME,LOAD_GAME,OPTIONS});

    MenuItem[] subItems;

    MenuItem() {
        subItems = null;
    }

    MenuItem(MenuItem[] subItems) {
        this.subItems = subItems;
    }

    public MenuItem[] getSubItems() {
        return subItems;
    }
}
