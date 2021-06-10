import './styles.scss';
import {Spinner} from "react-bootstrap";

export const LoadingIndicator = () => {
    return (
        <div className='loading--indicator--wrapper'>
            <div className='loading--indicator'>
                <Spinner animation="border" role="status">
                    <span className="sr-only">Loading...</span>
                </Spinner>
            </div>
        </div>
    )
}