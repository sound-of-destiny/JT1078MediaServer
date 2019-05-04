'use strict';

// Put variables in global scope to make them available to the browser console.
const audio = document.querySelector('audio');

const constraints = window.constraints = {
    audio: true,
    video: false
};

const instantMeter = document.querySelector('#instant meter');
const slowMeter = document.querySelector('#slow meter');
const clipMeter = document.querySelector('#clip meter');

const instantValueDisplay = document.querySelector('#instant .value');
const slowValueDisplay = document.querySelector('#slow .value');
const clipValueDisplay = document.querySelector('#clip .value');

try {
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    window.audioContext = new AudioContext();
} catch (e) {
    alert('Web Audio API not supported.');
}

function handleSuccess(stream) {
    // Put variables in global scope to make them available to the
    // browser console.

    const audioTracks = stream.getAudioTracks();
    console.log('Got stream with constraints:', constraints);
    console.log('Using audio device: ' + audioTracks[0].label);
    //console.log('audioTracks : ' + audioTracks[0].);
    stream.oninactive = function() {
        console.log('Stream ended');
    };

    window.stream = stream; // make variable available to browser console
    audio.srcObject = stream;

    const soundMeter = window.soundMeter = new SoundMeter(window.audioContext);
    soundMeter.connectToSource(stream, function(e) {
        if (e) {
            alert(e);
            return;
        }
        setInterval(() => {
            instantMeter.value = instantValueDisplay.innerText = soundMeter.instant.toFixed(2);
            slowMeter.value = slowValueDisplay.innerText = soundMeter.slow.toFixed(2);
            clipMeter.value = clipValueDisplay.innerText = soundMeter.clip;
        }, 200);
    });
}

function handleError(error) {
    console.log('navigator.MediaDevices.getUserMedia error: ', error.message, error.name);
}

navigator.mediaDevices.getUserMedia(constraints).then(handleSuccess).catch(handleError);

