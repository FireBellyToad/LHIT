package faust.lhipgame.menu.enums;

public enum MenuItem {
    YES,
    NO,
    BACK,
    MUSIC_TOGGLE,
    SOUND_TOGGLE,
    CREDITS,
    NEW_GAME("Are you sure?",new MenuItem[]{YES,NO}),
    LOAD_GAME,
    OPTIONS("Options",new MenuItem[]{BACK, MUSIC_TOGGLE,SOUND_TOGGLE}),
    MAIN(new MenuItem[]{NEW_GAME,LOAD_GAME,OPTIONS,CREDITS});

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
