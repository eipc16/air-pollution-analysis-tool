
import os
from quart import Quart, request, jsonify
from sources import MOD043KDataSource
import asyncio

sources_config = {
    'MOD04_3K': {
        'processor': MOD043KDataSource()
    }
    # 'MOD04_L2': {
    #     'processor': MOD04L2DataSource()
    # }
}

BASE_DATA_DIRECTORY = './data'

loop = asyncio.new_event_loop()
asyncio.set_event_loop(loop)
app = Quart(__name__)


@app.route("/sources")
async def sources():
    sources_list = list(map(lambda x: { 'name': x}, sources_config.keys()))
    return jsonify(sources_list)


@app.route("/sources/<source>/parameters")
async def parameters(source):
    if source not in sources_config.keys():
        raise Exception(f'No source with name: {source}')
    parameters_set = sources_config[source]['processor'].get_parameters_config().keys()
    parameters_list = list(map(lambda x: { 'name': x}, parameters_set))
    return jsonify(parameters_list)


@app.route("/measurements", methods=['POST'])
async def measurements():
    args = await request.get_json()

    source = args['source']
    parameter = args['parameter']

    if source not in sources_config.keys():
        raise Exception(f'No source with name: {source}')
    if parameter not in sources_config[source]['processor'].get_parameters_config().keys():
        raise Exception(f'Source {source} doesn\'t have a parameter: {parameter}')

    asyncio.get_event_loop().run_in_executor(None, _async_process_data, args)

    return jsonify({})


def _async_process_data(args):
    sources_config[args['source']]['processor'].fetch_data(
        order_id=args['orderId'],
        target_topic=args['targetTopic'],
        parameter=args['parameter'],
        date_from=args['dateFrom'],
        date_to=args['dateTo'],
        latitude_range=[float(args['bottomLatitude']), float(args['upperLatitude'])],
        longitude_range=[float(args['bottomLongitude']), float(args['upperLongitude'])]
    )
    return 'Success'


def get_env_variable(name, default_value=None):
    if name in os.environ:
        return os.environ[name]
    return default_value


if __name__ == '__main__':
    app.run(debug=get_env_variable('ENABLE_DEBUG', False),
            port=get_env_variable('SERVER_PORT', 5000))
