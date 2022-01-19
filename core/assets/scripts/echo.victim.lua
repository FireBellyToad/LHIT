--"name": "Discipulus victim",
--"description": "An harmless victim decapitated by the disciupuls of script echo.discipuls.json",

function doStep0 (echoActor, roomContent)
   --
end

function doStep1 (echoActor, roomContent)
  --
end

function doStep2 (echoActor, roomContent)
  --
end

function doStep3 (echoActor, roomContent)
  --
end

function doStep4 (echoActor, roomContent)
end

function onEchoEnd (echoActor, roomContent)
    echoActor:spawnInstance("ECHO_CORPSE",0,8,true);
end
