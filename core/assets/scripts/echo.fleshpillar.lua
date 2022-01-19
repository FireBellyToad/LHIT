--"name": "Infernum flesh pillar",
--"description": "a flesh pillar that bounces the player around",

function doStep0 (echoActor, roomContent)
end

function doStep1 (echoActor, roomContent)
end

function doStep2 (echoActor, roomContent)
    if( echoActor:isCheckTrue("UNTIL_PLAYER_DAMAGE_IS_MORE_THAN","5",roomContent) and
            echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","ESCAPE_PORTAL",roomContent)) then
        echoActor:setNewIndex(0);
    end
end
