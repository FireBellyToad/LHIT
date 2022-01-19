-- "name": "Horror",
-- "description": "Darkness stays until Willowisp created by (echo.horrorbody.json) is killed"

function doStep0 (echoActor, roomContent)
    if( not echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","WILLOWISP",roomContent)) then
        echoActor:setNewIndex(4);
    end
end


function doStep1 (echoActor, roomContent)
    if( not echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","WILLOWISP",roomContent)) then
        echoActor:setNewIndex(4);
    end
end


function doStep2 (echoActor, roomContent)
    if( not echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","WILLOWISP",roomContent)) then
        echoActor:setNewIndex(4);
    end
end


function doStep3 (echoActor, roomContent)
    if( not echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","WILLOWISP",roomContent)) then
        echoActor:setNewIndex(4);
    end
end

function doStep4 (echoActor, roomContent )
    if( echoActor:isCheckTrue("UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE","WILLOWISP",roomContent)) then
        echoActor:setNewIndex(0);
    end
end
