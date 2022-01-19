--   "name": "Willowisp spawn",
--  "description": "Willowisp spawns in the boat map",

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

end

function onEchoEnd (echoActor, roomContent)
    echoActor:spawnInstance("WILLOWISP",0,8,true);
end
