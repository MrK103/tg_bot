let current = null;
document.querySelector('#username').addEventListener('focus', function(e) {
    if (current) current.pause();
    current = anime({
        targets: 'path',
        strokeDashoffset: {
            value: 0,
            duration: 700,
            easing: 'easeOutQuart'
        },
        strokeDasharray: {
            value: '240 1386',
            duration: 700,
            easing: 'easeOutQuart'
        }
    });
});
document.querySelector('#password').addEventListener('focus', function(e) {
    if (current) current.pause();
    current = anime({
        targets: 'path',
        strokeDashoffset: {
            value: -336,
            duration: 700,
            easing: 'easeOutQuart'
        },
        strokeDasharray: {
            value: '240 1386',
            duration: 700,
            easing: 'easeOutQuart'
        }
    });
});

document.querySelector('#submit').addEventListener('focus', function(e) {
    if (current) current.pause();
    current = anime({
        targets: 'path',
        strokeDashoffset: {
            value: -730,
            duration: 700,
            easing: 'easeOutQuart'
        },
        strokeDasharray: {
            value: '530 1386',
            duration: 700,
            easing: 'easeOutQuart'
        }
    });
});

async function init () {
    const node = document.querySelector("#type-text")

    await sleep(1500)
    node.innerText = ""
    await node.type('Hello, ')

    while (true) {
        await node.type('KATA!')
        await sleep(2000)
        await node.delete('KATA!')
        await node.type('World!')
        await sleep(2000)
        await node.delete('World!')
    }
}

const sleep = time => new Promise(resolve => setTimeout(resolve, time))

class TypeAsync extends HTMLSpanElement {
    get typeInterval () {
        const randomMs = 100 * Math.random()
        return randomMs < 50 ? 10 : randomMs
    }

    async type (text) {
        for (let character of text) {
            this.innerText += character
            await sleep(this.typeInterval)
        }
    }

    async delete (text) {
        for (let character of text) {
            this.innerText = this.innerText.slice(0, this.innerText.length -1)
            await sleep(this.typeInterval)
        }
    }
}

customElements.define('type-async', TypeAsync, { extends: 'span' })


init()