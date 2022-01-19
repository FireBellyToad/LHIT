--   "name": "Portal spawner",
--  "description": "Act as an exit to echo.infernum.lua",


function doStep0 (echoActor, roomContent)
    echoActor:setInvisible();
    echoActor:setLayerToShow("ECHO_LAYER");
    echoActor:showTextBox("echo.infernum.first");
end


function doStep1 (echoActor, roomContent)
    echoActor:showTextBox("echo.infernum.second");
    roomContent.player:hurt(echoActor);
end


function doStep2 (echoActor, roomContent)
    if( echoActor:isCheckTrue("UNTIL_PLAYER_DAMAGE_IS_MORE_THAN","5",roomContent) and
            echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","ESCAPE_PORTAL",roomContent)) then
        echoActor:setNewIndex(0);
    end
end
