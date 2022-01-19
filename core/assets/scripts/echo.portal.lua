--   "name": "Willowisp spawn",
--  "description": "Willowisp spawns in the boat map",

function doStep0 (echoActor, roomContent, stepReference)
    echoActor:setInvisible();
    echoActor:spawnInstance("ESCAPE_PORTAL",0,0,true);
end