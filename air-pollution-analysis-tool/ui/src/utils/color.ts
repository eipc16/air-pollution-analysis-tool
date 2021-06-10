
const hashCode = (str: string) => {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
}

const intToRGB = (intVal: number) => {
    var c = (intVal & 0x00FFFFFF)
        .toString(16)
        .toUpperCase();

    return "00000".substring(0, 6 - c.length) + c;
}

const stringToRGB = (str: string) => {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    var colour = '#';
    for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 0xFF;
        colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
}

// const stringToRGB = (str: string) => {
//     console.log(str);
//     const strHashCode = hashCode(str);
//     return intToRGB(strHashCode);
// }

export default stringToRGB;