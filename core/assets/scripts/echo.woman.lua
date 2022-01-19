--  "name": "Screaming woman",
--  "description": "A woman screams and gets teared apart by unseen forces"

function doStep0 (echoActor, roomContent)
    echoActor:showTextBox("echo.woman.first");
    echoActor:move("RIGHT", 60);
end


function doStep1 (echoActor, roomContent)
    echoActor:move("UNUSED", 0);
end


function doStep2 (echoActor, roomContent)
    echoActor:showTextBox("echo.woman.second");
end


function doStep3 (echoActor, roomContent)
  --
end

function doStep4 (echoActor, roomContent)

end
